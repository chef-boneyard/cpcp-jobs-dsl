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
        shell '''
            #!/bin/bash
            chef exec foodcritic .
        '''.stripIndent().trim()
        shell '''
            #!/bin/bash
            chef exec rspec spec/
        '''.stripIndent().trim()
    }

    wrappers {
        colorizeOutput()
        preBuildCleanup()
    }

    triggers {
        cron('@midnight')
    }
}
