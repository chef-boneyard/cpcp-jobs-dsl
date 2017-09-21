// Specify basic variables to be used in the DSL
String name = 'ilo-cookbook'
String repourl = 'https://github.com/Icinga/chef-icinga2.git'

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
        shell readFileFromWorkspace('resources/bundle_exec_rake_style.sh')
        shell readFileFromWorkspace('resources/bundle_exec_rake_unit.sh')
    }

    wrappers {
        colorizeOutput()
        preBuildCleanup()
    }

    triggers {
        cron('@midnight')
    }
}