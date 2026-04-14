# A prototype of orchestrator for MOSAICO. 

### Installation Tips

 * The Systems-Modeling/SysML-v2-Pilot-Implementation is distributed on maven.pkg.github.com . 
To be able to access it, follow the instructions given at https://docs.github.com/fr/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry .
Specifically: https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry#authenticating-with-a-personal-access-token

    (Your token needs the `read:packages` permission.)

 * Also check the path of the library stored in the variable `libPrefix` of `SysMLDecoder.java` .

 * The LLM can be chosen in the `SolutionAgent` constructor.