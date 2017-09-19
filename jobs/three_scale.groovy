// Specify basic variables to be used in the DSL
String name = '3scale-cookbook'
String repourl = 'https://github.com/3scale/chef-3scale.git'

def kitchenFile = readFileFromWorkspace('tk/3scale.yml')

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
        shell sprintf('#!/bin/bash\ncat << EOF > .kitchen.azure.yml\n%s\nEOF', kitchenFile)
        shell '''
            #!/bin/bash
            eval "$(direnv export bash)"
            chef exec gem install kitchen-azurerm -N
            KITCHEN_YAML=".kitchen.azure.yml" chef exec kitchen test -c
        '''.stripIndent().trim()        
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
            task('Class: Kitchen::ActionFailed', '''
            #!/bin/bash
            KITCHEN_YAML=".kitchen.azure.yml" chef exec kitchen destroy all
            '''.stripIndent().trim())
        }
    }    
}