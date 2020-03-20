package cytomine.core.project

class ProjectLastActivity {

    Project project

    Date lastActivity

    static constraints = {
        project(unique: true)
    }
}
