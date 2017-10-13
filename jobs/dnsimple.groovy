// Specify basic variables to be used in the DSL
String name = 'dnsimple-cookbook'
String repourl = 'https://github.com/dnsimple/chef-dnsimple.git'

def kitchenFile = readFileFromWorkspace('tk/dnsimple.yml')

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
        environmentVariables {
            env('KITCHEN_YAML', '.kitchen.azure.yml')
        }
        shell readFileFromWorkspace('resources/check_for_md_files.sh')
        shell sprintf('#!/bin/bash\ncat << EOF > .kitchen.azure.yml\n%s\nEOF', kitchenFile)
        shell '''
            #!/bin/bash
            echo "###### INSTALL GEMS ######"
            chef exec gem install dnsimple -N
            chef exec gem install kitchen-azurerm -N
        '''.stripIndent().trim()
        shell '''
            #!/bin/bash
            echo "###### STYLE: RUBOCOP ######"
            chef exec rake style:rubocop
        '''.stripIndent().trim()
        shell '''
            #!/bin/bash
            echo "###### STYLE: FOODCRTIC ######"
            chef exec rake style:foodcritic
        '''.stripIndent().trim()
        shell '''
            #!/bin/bash
            echo "###### UNIT: CHEFSPEC ######"
            chef exec rake unit:chefspec
        '''.stripIndent().trim()
        shell '''
            #!/bin/bash
            echo "###### INTEGRATION: TEST KITCHEN ######"
            KITCHEN_YAML=".kitchen.azure.yml" chef exec rake kitchen
        '''.stripIndent().trim()
    }

    wrappers {
        colorizeOutput()
        preBuildCleanup()
        credentialsBinding {
            string("AZURE_CLIENT_ID", "AZURE_CLIENT_ID")
            string("AZURE_CLIENT_SECRET", "AZURE_CLIENT_SECRET")
            string("AZURE_TENANT_ID", "AZURE_TENANT_ID")
            string("DNSIMPLE_ACCESS_TOKEN", "DNSIMPLE_ACCESS_TOKEN")
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