#!/bin/bash
eval "$(direnv export bash)"

# Install gems using bundle if a Gemfile exists
if [ -f Gemfile ]
then
    bundle install
fi