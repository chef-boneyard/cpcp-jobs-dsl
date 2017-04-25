#!/bin/bash
echo "2.3.1" > .ruby-version
echo "use_ruby" > .envrc
direnv allow .
eval "$(direnv export bash)"

ruby --version

gem install bundler -N
gem install rake -N
gem install github_api -N
bundle