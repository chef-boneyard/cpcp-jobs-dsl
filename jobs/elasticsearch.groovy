// Specify basic variables to be used in the DSL
String name = 'elasticsearch-cookbook'
String repourl = 'https://github.com/elastic/cookbook-elasticsearch.git'

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
        shell readFileFromWorkspace('resources/envrc_setup.sh')
        shell readFileFromWorkspace('resources/bundle_exec_rake_style.sh')
        shell readFileFromWorkspace('resources/bundle_exec_rake_spec.sh')
        shell '''
            #!/bin/bash
            cat << EOF > .kitchen.azure.yml
            driver:
            name: azurerm

            driver_config:
            subscription_id: '1e0b427a-d58b-494e-ae4f-ee558463ebbf'
            location: 'East US'
            machine_size: 'Standard_D1'

            transport:
            ssh_key: ~/.ssh/id_kitchen-azurerm

            provisioner:
            name: chef_zero
            nodes_path: 'test/fixtures/nodes'
            data_bags_path: 'test/fixtures/data_bags'
            environments_path: 'test/fixtures/environments'
            client_rb:
                environment: _default
            attributes:
                java:
                jdk_version: '7'

            platforms:
            - name: ubuntu-14.04
                run_list:
                - recipe[apt]
                - recipe[curl]
            - name: ubuntu-12.04
                run_list:
                - recipe[apt]
                - recipe[curl]
            - name: centos-6.7
                run_list:
                - recipe[yum]
                - recipe[curl]
                - recipe[elasticsearch_test::fix_nss] # see recipe header

            suites:
            - name: package # install from package
                require_chef_omnibus: 12.6.0
                run_list:
                - recipe[java]
                - recipe[elasticsearch_test::default_with_plugins]
                attributes:
                elasticsearch:
                    install_type: package

            - name: tarball # install by tarball
                require_chef_omnibus: 12.6.0
                run_list:
                - recipe[java]
                - recipe[elasticsearch_test::default_with_plugins]
                attributes:
                elasticsearch:
                    install_type: tarball

            - name: override_package # the override-everything use case
                driver_config:
                require_chef_omnibus: 12.6.0
                run_list:
                - recipe[java]
                - recipe[elasticsearch_test::package]

            - name: override_tarball # the override-everything use case
                driver_config:
                require_chef_omnibus: 12.6.0
                run_list:
                - recipe[java]
                - recipe[elasticsearch_test::tarball]

            - name: chef-12.5.1
                driver_config:
                require_chef_omnibus: 12.5.1
                includes:
                - ubuntu-14.04
                run_list:
                - recipe[java]
                - recipe[elasticsearch_test::default_with_plugins]

            - name: chef-12.4.3
                driver_config:
                require_chef_omnibus: 12.4.3
                includes:
                - ubuntu-14.04
                run_list:
                - recipe[java]
                - recipe[elasticsearch_test::default_with_plugins]

            - name: chef-12.3.0
                driver_config:
                require_chef_omnibus: 12.3.0
                includes:
                - ubuntu-14.04
                run_list:
                - recipe[java]
                - recipe[elasticsearch_test::default_with_plugins]

            - name: chef-12.2.1
                driver_config:
                require_chef_omnibus: 12.2.1
                includes:
                - ubuntu-14.04
                run_list:
                - recipe[java]
                - recipe[elasticsearch_test::default_with_plugins]

            - name: chef-12.1.2
                driver_config:
                require_chef_omnibus: 12.1.2
                includes:
                - ubuntu-14.04
                run_list:
                - recipe[java]
                - recipe[elasticsearch_test::default_with_plugins]

            - name: shieldwatcher # install licensed plugins for testing
                require_chef_omnibus: true
                run_list:
                - recipe[java]
                - recipe[elasticsearch_test::shieldwatcher]
                attributes:
                elasticsearch:
                    install_type: package            
            EOF
        '''.stripIndent().trim()
        shell '''
            #!/bin/bash
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