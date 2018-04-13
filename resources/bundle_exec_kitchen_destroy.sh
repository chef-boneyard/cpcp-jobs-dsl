#!/bin/bash
eval "$(direnv export bash)"
KITCHEN_YAML=".kitchen.azure.yml" bundle exec kitchen destroy all