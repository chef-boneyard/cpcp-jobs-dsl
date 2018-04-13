#!/bin/bash
bundle exec gem install kitchen-azurerm -N
KITCHEN_YAML=".kitchen.azure.yml" bundle exec kitchen test -c