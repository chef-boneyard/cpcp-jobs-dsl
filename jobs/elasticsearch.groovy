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
        conditionalSteps {
            condition {
                alwaysRun()
            }
            steps {
                shell readFileFromWorkspace('resources/check_for_md_files.sh')
            }
            runner ('Fail')
        }

        conditionalSteps {
            condition {
                alwaysRun()
            }
            steps {
                shell readFileFromWorkspace('resources/envrc_setup.sh')
            }
            runner ('Fail')
        }

        conditionalSteps {
            condition {
                alwaysRun()
            }
            steps {
                shell readFileFromWorkspace('resources/bundle_exec_rake_style.sh')
            }
            runner ('Fail')
        }

        conditionalSteps {
            condition {
                alwaysRun()
            }
            steps {
                shell readFileFromWorkspace('resources/bundle_exec_rake_spec.sh')
            }
            runner ('Fail')
        }        
        
        conditionalSteps {
            condition {
                alwaysRun()
            }
            steps {
                shell sprintf('#!/bin/bash\ncat << EOF > .kitchen.azure.yml\n%s\nEOF', kitchenFile)
            }
            runner ('Fail')
        } 

        conditionalSteps {
            condition {
                alwaysRun()
            }
            steps {
                shell readFileFromWorkspace('resources/bundle_exec_kitchen_test.sh')
            }
            runner ('Fail')
        }         
        
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