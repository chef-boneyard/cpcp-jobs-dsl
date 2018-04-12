// Specify basic variables to be used in the DSL
String name = 'rackspace_monitoring-cookbook'
String repourl = 'https://github.com/rackspace-cookbooks/rackspace_monitoring.git'

def kitchenFile = readFileFromWorkspace('tk/rackspace_monitoring.yml')

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
        shell readFileFromWorkspace('resources/chef_exec_rubocop.sh')
        shell readFileFromWorkspace('resources/chef_exec_foodcritic.sh')
        shell readFileFromWorkspace('resources/chef_exec_rspec_spec.sh')
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