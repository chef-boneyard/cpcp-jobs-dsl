// Specify basic variables to be used in the DSL
String name = 'gocd-cookbook'
String repourl = 'https://github.com/gocd/go-cookbook'

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
        shell readFileFromWorkspace('resources/bundle_exec_rake_chefspec.sh')
    }

    wrappers {
        colorizeOutput()
        preBuildCleanup()
    }

    triggers {
        cron('@midnight')
    }
}