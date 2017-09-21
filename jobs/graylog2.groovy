// Specify basic variables to be used in the DSL
String name = 'graylog2-cookbook'
String repourl = 'https://github.com/Graylog2/graylog2-cookbook.git'

def kitchenFile = readFileFromWorkspace('tk/graylog2.yml')

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
        shell readFileFromWorkspace('resources/chef_exec_rake_spec.sh')
        shell sprintf('#!/bin/bash\ncat << EOF > .kitchen.azure.yml\n%s\nEOF', kitchenFile)
        shell readFileFromWorkspace('resources/chef_exec_kitchen_test.sh')
    }

    wrappers {
        colorizeOutput()
        preBuildCleanup()
        credentialsBinding {
            string("AZURE_CLIENT_ID", "AZURE_CLIENT_ID")
            string("AZURE_CLIENT_SECRET", "AZURE_CLIENT_SECRET")
            string("AZURE_TENANT_ID", "AZURE_TENANT_ID")
        }        
    }

    triggers {
        cron('@midnight')
    }

    publishers {
        postBuildTask {
            task('Class: Kitchen::ActionFailed', readFileFromWorkspace('resources/chef_exec_kitchen_destroy.sh'))
        }
    }

}