// Specify basic variables to be used in the DSL
String name = 'alteryx-cookbook'
String repourl = 'https://github.com/alteryx/cookbook-alteryx-server.git'

freeStyleJob(name) {

    label('chefdk')

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
        shell readFileFromWorkspace('resources/chef_exec_rspec_spec.sh')
    }

    wrappers {
        colorizeOutput()
        preBuildCleanup()
    }

    triggers {
        cron('@midnight')
    }
}