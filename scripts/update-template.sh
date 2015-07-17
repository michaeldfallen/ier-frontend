thisDir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$thisDir/.."

function exit_err() {
  [ $# -gt 0 ] && echo "fatal: $*" 1>&2
  exit 1
}

function removeSubmodule() {
  submodule_name=$1
  if git submodule status "$submodule_name" >/dev/null 2>&1; then
    git submodule deinit -f "$submodule_name"
    git rm -f "$submodule_name"
   
    git config -f .gitmodules --remove-section "submodule.$submodule_name"
    if [ -z "$(cat .gitmodules)" ]; then
      git rm -f .gitmodules
    else
      git add .gitmodules
    fi
  else
    exit_err "Submodule '$submodule_name' not found"
  fi
}

function ensureDeleted() {
  submoduleName=$1
  if git submodule status "$submoduleName" >/dev/null 2>&1; then
    removeSubmodule "$submoduleName"
  fi
  if [ -e "$submoduleName" ]; then
    rm -r "$submoduleName"
  fi
}

function checkSubmoduleExists() {
  submoduleUrl=$1
  submoduleName=$2
  if [ -z "$(cat .gitmodules | grep "$submoduleUrl")" ]; then
    removeSubmodule "$submoduleName"
    git submodule add "$submoduleUrl" $submoduleName
  fi
}

ensureDeleted "app/assets/govuk_template_play"
checkSubmoduleExists "https://github.com/alphagov/govuk_frontend_toolkit.git" "app/assets/govuk_frontend_toolkit"
ensureDeleted "app/assets/mustache/govuk_template_mustache"

echo "Updating govuk_template_play"
git submodule init
git submodule update
