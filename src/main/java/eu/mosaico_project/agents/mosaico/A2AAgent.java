package eu.mosaico_project.agents.mosaico;


import eu.mosaico_project.miol.ChannelState;
import eu.mosaico_project.miol.task.AgentTask;
import eu.mosaico_project.miol.task.output.Channel;
import eu.mosaico_project.miol.task.output.TaskOutput;
import eu.mosaico_project.miol.task.output.value.MultipleValue;
import eu.mosaico_project.miol.task.output.value.StringValue;
import io.a2a.client.*;
import io.a2a.client.config.ClientConfig;
import io.a2a.client.http.A2ACardResolver;
import io.a2a.client.http.A2AHttpClient;
import io.a2a.client.http.JdkA2AHttpClient;
import io.a2a.client.transport.grpc.GrpcTransport;
import io.a2a.client.transport.grpc.GrpcTransportConfig;
import io.a2a.client.transport.jsonrpc.JSONRPCTransport;
import io.a2a.client.transport.jsonrpc.JSONRPCTransportConfig;
import io.a2a.client.transport.rest.RestTransport;
import io.a2a.client.transport.rest.RestTransportConfig;
import io.a2a.spec.*;
import io.grpc.ManagedChannelBuilder;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A proxy to talk with a remote A2A agent.
 */
public class A2AAgent extends MosaicoAgent {

    final URL url;
    final int timeout = 2;
    final List<String> outputs = new ArrayList<>();

    public A2AAgent(URL url, String id, String name, Licence licence, List<String> constraints) {
        super(id, name, licence, constraints);
        this.url = url;
    }

    static List<BiConsumer<ClientEvent, AgentCard>> getConsumers(
            final CompletableFuture<String> messageResponse, List<String> outputs
    ) {
        List<BiConsumer<ClientEvent, AgentCard>> consumers = new ArrayList<>();
        consumers.add(
                (event, agentCard) -> {
                    if (event instanceof MessageEvent messageEvent) {
                        Message responseMessage = messageEvent.getMessage();
                        String text = extractTextFromParts(responseMessage.getParts());
                        System.out.println("[LOG] Consume message: " + text);
                        messageResponse.complete(text);
                    } else if (event instanceof TaskUpdateEvent taskUpdateEvent) {
                        UpdateEvent updateEvent = taskUpdateEvent.getUpdateEvent();
                        if (updateEvent
                                instanceof TaskStatusUpdateEvent taskStatusUpdateEvent) {
                            System.out.println(
                                    "[LOG] Consume status-update: "
                                            + taskStatusUpdateEvent.getStatus().state().asString());
                            if (taskStatusUpdateEvent.isFinal()) {
                                StringBuilder textBuilder = new StringBuilder();
                                List<Artifact> artifacts
                                        = taskUpdateEvent.getTask().getArtifacts();
                                for (Artifact artifact : artifacts) {
                                    textBuilder.append(extractTextFromParts(artifact.parts()));
                                }
                                String text = textBuilder.toString();
                                messageResponse.complete(text);
                            }
                        } else if (updateEvent instanceof TaskArtifactUpdateEvent
                                taskArtifactUpdateEvent) {
                            List<Part<?>> parts = taskArtifactUpdateEvent
                                    .getArtifact()
                                    .parts();
                            String text = extractTextFromParts(parts);
                            System.out.println("[LOG] Consume artifact-update: " + text);
                        }
                    } else if (event instanceof TaskEvent taskEvent) {
                        System.out.println("[LOG]  Received a task event: "
                                + taskEvent.getTask().getId() + " " + taskEvent.getTask().getStatus().state().toString());
                        var artifacts = taskEvent.getTask().getArtifacts();
                        for (Artifact a : artifacts)
                            for (Part p : a.parts()) {
                                if (p instanceof TextPart tp) {
                                    String res = tp.getText();
                                    System.out.println("[LOG] Artifact part received: " + res);
                                    outputs.add(res);
                                }
                            }
                    }
                });
        return consumers;
    }

    static String extractTextFromParts(final List<Part<?>> parts) {
        final StringBuilder textBuilder = new StringBuilder();
        if (parts != null) {
            for (final Part<?> part : parts) {
                if (part instanceof TextPart textPart) {
                    textBuilder.append(textPart.getText());
                }
            }
        }
        return textBuilder.toString();
    }

    @Override
    public TaskOutput performTask(AgentTask task, ChannelState dependencies, Channel channel) {
        System.out.println("[VERSION] A2A agent using A2A spec version 0.3.0.");

        String content = task.getTaskDescription();
        String replyToUrl = "FIXME:URL"; // FIXME

        try {
            System.out.println("[LOG] Connecting to agent at: " + A2AAgent.this.url);
            AgentCard publicAgentCard =
                    new A2ACardResolver(A2AAgent.this.url.url()).getAgentCard();
            System.out.println("[LOG] Successfully fetched public agent card");
            System.out.println(publicAgentCard);

            // Create a CompletableFuture to handle async response
            final CompletableFuture<String> messageResponse
                    = new CompletableFuture<>();

            // Create consumers for handling client events
            List<BiConsumer<ClientEvent, AgentCard>> consumers
                    = getConsumers(messageResponse, this.outputs);

            // Create error handler for streaming errors
            Consumer<Throwable> streamingErrorHandler = (error) -> {
                System.out.println("[ERROR] Streaming error occurred: " + error.getMessage());
                messageResponse.completeExceptionally(error);
            };

            // Create channel factory for gRPC transport
            Function<String, io.grpc.Channel> channelFactory = agentUrl -> {
                return ManagedChannelBuilder.forTarget(agentUrl).usePlaintext().build();
            };

            ClientConfig clientConfig = new ClientConfig.Builder()
                    .setAcceptedOutputModes(List.of("Text"))
                    .setPushNotificationConfig(new PushNotificationConfig(replyToUrl, null, null, null))
                    .build();

            A2AHttpClient customHttpClient = new JdkA2AHttpClient();

            // Create the client with several transport support.
            Client client = Client.builder(publicAgentCard)
                    .addConsumers(consumers)
                    .streamingErrorHandler(streamingErrorHandler)
                    .withTransport(GrpcTransport.class,
                            new GrpcTransportConfig(channelFactory))
                    .withTransport(JSONRPCTransport.class,
                            new JSONRPCTransportConfig(customHttpClient))
                    .withTransport(RestTransport.class, new RestTransportConfig())
                    .clientConfig(clientConfig)
                    .build();

            // Create and send the message
            TextPart p = new TextPart(content);

            Message.Builder messageBuilder = (new Message.Builder()).role(Message.Role.AGENT).parts(Collections.singletonList(p));
            Message message = messageBuilder.build();

            System.out.println("[LOG] Sending message: " + content);
            client.sendMessage(message);
            System.out.println("[LOG] Message sent successfully. Handling sync response. Timout= " + this.timeout + " seconds.");

            try {
                // Wait for response with timeout
                String responseText = messageResponse.get(this.timeout, TimeUnit.SECONDS); // FIXME : return on task completion instead.
                System.out.println("[LOG] Synchronous response: " + responseText);
            } catch (Exception e) {
                System.err.println("[WARNING] No sync answer before timeout.");
            }

        } catch (Exception e) {
            System.err.println("[Error] " + e.getMessage());
        }

        MultipleValue mult = new MultipleValue();
        for (String s : outputs) mult.addValue(new StringValue(s));
        System.out.println("[LOG] Agent output: " + mult + " on channel " + channel.name());
        return new TaskOutput(task, channel, mult);
    }

    public record URL(String url) {
    }

}
