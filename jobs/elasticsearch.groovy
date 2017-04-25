// Specify basic variables to be used in the DSL
String name = 'elasticsearch-cookbook'
String repourl = 'https://github.com/elastic/cookbook-elasticsearch.git'

freeStyleJob(name) {
    scm {
        git {
            remote {
                name('origin')
                url(repourl)
            }
            clean()
            branch('master')
        }
    }
    steps {
        shell readFileFromWorkspace('resources/check_for_md_files.sh')
        shell readFileFromWorkspace('resources/envrc_setup.sh')
        shell readFileFromWorkspace('resources/bundle_exec_rake_style.sh')
        shell readFileFromWorkspace('resources/bundle_exec_rake_spec.sh')
        shell '''
            #!/bin/bash
            cp /home/jenkins/kitchens/elasticsearch.kitchen.yml .kitchen.azure.yml
            KITCHEN_YAML=".kitchen.azure.yml" chef exec kitchen test -c
        '''.stripIndent().trim()
        shell '''
            #!/bin/bash
            KITCHEN_YAML=".kitchen.azure.yml" chef exec kitchen destroy all
        '''.stripIndent().trim()        
    }
    wrappers {
        colorizeOutput()
        preBuildCleanup()
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