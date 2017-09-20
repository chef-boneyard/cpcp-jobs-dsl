#!/bin/bash
chef exec gem install kitchen-azurerm -N
KITCHEN_YAML=".kitchen.azure.yml" chef exec kitchen test -c