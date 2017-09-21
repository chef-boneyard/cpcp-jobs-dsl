// Specify basic variables to be used in the DSL
String name = 'oneview-cookbook'
String repourl = 'https://gitlab.com/gitlab-org/cookbook-omnibus-gitlab.git'

freeStyleJob(name) {

    scm {
        git {
            remote {
                url(repourl)
            }
            branch('master')
        }
    }

    steps {
        shell readFileFromWorkspace('resources/check_for_md_files.sh')
        shell readFileFromWorkspace('resources/envrc_setup.sh')
        shell readFileFromWorkspace('resources/bundle_exec_rake_style')
        shell readFileFromWorkspace('resources/bundle_exec_rake_unit')
    }

    wrappers {
        colorizeOutput()
        preBuildCleanup()
    }

    triggers {
        cron('@midnight')
    }
}