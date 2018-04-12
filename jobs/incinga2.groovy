// Specify basic variables to be used in the DSL
String name = 'icinga2-cookbook'
String repourl = 'https://github.com/Icinga/chef-icinga2.git'

freeStyleJob(name) {

    label('ruby')

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
        shell readFileFromWorkspace('resources/bundle_exec_rake_rubocop.sh')
        shell readFileFromWorkspace('resources/bundle_exec_rake_foodcritic.sh')
        shell readFileFromWorkspace('resources/bundle_exec_rake_spec.sh')
    }

    wrappers {
        colorizeOutput()
        preBuildCleanup()
    }

    triggers {
        cron('@midnight')
    }
}