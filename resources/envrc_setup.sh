#!/bin/bash

# This script is responsible for configuring the Ruby environment when not using ChefDK
# As the version can change based on the agent container there is logic to ensure
# that the version of ruby used is always set correctly

# Source the Chruby shell script
source /usr/local/share/chruby/chruby.sh

# Get the version of ruby that is installed
# This will be something like
#      ruby-2.3.5
# And it will be trimmed and split to leave 2.3.5
RUBY_VERSION=`chruby | tr -d '[:space:]' | awk -F "-" '{print $2}'`

echo $RUBY_VERSION > .ruby-version
echo "use_ruby" > .envrc
direnv allow .
eval "$(direnv export bash)"

ruby --version

gem install bundler -N
gem install rake -N
gem install github_api -N

# Call bundle, but only if the a Gemfile exists
if [ -f Gemfile ]
then
    bundle
fi