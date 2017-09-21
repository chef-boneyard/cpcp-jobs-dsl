#!/bin/bash
eval "$(direnv export bash)"
bundle exec rake unit:chefspec