// Specify basic variables to be used in the DSL
String name = 'elasticsearch-cookbook'
String repourl = 'https://github.com/elastic/cookbook-elasticsearch.git'

def kitchenFile = readFileFromWorkspace('tk/elasticsearch.yml')

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
        singleConditionalBuilder {
            condition {
                alwaysRun()
            }
            buildStep {
                shell readFileFromWorkspace('resources/check_for_md_files.sh')
            }
        }

        shell readFileFromWorkspace('resources/envrc_setup.sh')
        shell readFileFromWorkspace('resources/bundle_exec_rake_style.sh')
        shell readFileFromWorkspace('resources/bundle_exec_rake_spec.sh')
        shell sprintf('#!/bin/bash\ncat << EOF > .kitchen.azure.yml\n%s\nEOF', kitchenFile)
        shell readFileFromWorkspace('resources/bundle_exec_kitchen_test.sh')
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
            task('Class: Kitchen::ActionFailed', readFileFromWorkspace('resources/bundle_exec_kitchen_destroy.sh'))
        }
    }
}