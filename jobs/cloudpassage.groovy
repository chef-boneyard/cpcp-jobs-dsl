// Specify basic variables to be used in the DSL
String name = 'cloudpassage-cookbook'
String repourl = 'https://github.com/cloudpassage/cloudpassage-chef-cookbook.git'

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
        shell readFileFromWorkspace('resources/envrc_setup.sh')
        shell readFileFromWorkspace('resources/chef_exec_rake_style.sh')
        shell readFileFromWorkspace('resources/chef_exec_rake_spec.sh')
    }

    wrappers {
        colorizeOutput()
        preBuildCleanup()
    }

    triggers {
        cron('@midnight')
    }
}