package eu.mosaico_project.shadow_sysml;

import java.util.List;

public interface ActionUsage extends OccurenceUsage, Feature  {

    List<String> getDeclaredType(); // Fixme : string loses information -> replace with Type
}
