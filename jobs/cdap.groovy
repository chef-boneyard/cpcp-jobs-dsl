// Specify basic variables to be used in the DSL
String name = 'cdap-cookbook'
String repourl = 'https://github.com/caskdata/cdap_cookbook.git'

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
        shell readFileFromWorkspace('resources/bundle_install.sh')
        shell readFileFromWorkspace('resources/bundle_exec_rake.sh')
    }

    wrappers {
        colorizeOutput()
        preBuildCleanup()
    }

    triggers {
        cron('@midnight')
    }
}