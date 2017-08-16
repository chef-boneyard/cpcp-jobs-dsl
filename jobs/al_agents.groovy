// Specify basic variables to be used in the DSL
String name = 'al_agents-cookbook'
String repourl = 'https://github.com/alertlogic/al_agents.git'

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
        shell readFileFromWorkspace('resources/chef_exec_rubocop.sh')
        shell readFileFromWorkspace('resources/chef_exec_foodcritic.sh')
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