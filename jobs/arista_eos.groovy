// Specify basic variables to be used in the DSL
String name = 'arista-eos-cookbook'
String repourl = 'https://github.com/aristanetworks/chef-eos.git'

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
        shell readFileFromWorkspace('resources/chef_exec_rake_style.sh')
    }

    wrappers {
        colorizeOutput()
        preBuildCleanup()
    }

    triggers {
        cron('@midnight')
    }
}