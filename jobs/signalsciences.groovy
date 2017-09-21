// Specify basic variables to be used in the DSL
String name = 'signalsciences-cookbook'
String repourl = 'https://github.com/signalsciences/signalsciences-cookbook.git'

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