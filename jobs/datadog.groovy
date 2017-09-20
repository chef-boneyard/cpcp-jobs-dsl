// Specify basic variables to be used in the DSL
String name = 'datadog-cookbook'
String repourl = 'https://github.com/DataDog/chef-datadog.git'

def kitchenFile = readFileFromWorkspace('tk/datadog.yml')

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
        shell '''
        chef gem install coveralls
        chef gem instyall json_spec
        '''.stripIndent().trim()
        shell readFileFromWorkspace('resources/chef_exec_rubocop.sh')
        shell readFileFromWorkspace('resources/chef_exec_foodcritic.sh')
        shell readFileFromWorkspace('resources/chef_exec_rspec_spec.sh')
        shell sprintf('#!/bin/bash\ncat << EOF > .kitchen.azure.yml\n%s\nEOF', kitchenFile)
        shell readFileFromWorkspace('resources/chef_exec_kitchen_test.sh')
    }

    wrappers {
        colorizeOutput()
        preBuildCleanup()
    }

    triggers {
        cron('@midnight')
    }
}