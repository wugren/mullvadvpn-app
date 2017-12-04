#! /usr/bin/env bash

# Will make sure you have rustfmt at the version in $VERSION, then format all the source code.
# Run with --only-format as the first argument to skip checking rustfmt version.

set -u

VERSION="0.2.17"
CMD="rustfmt"
INSTALL_CMD="cargo install --vers $VERSION --force rustfmt-nightly"

case "$(uname -s)" in
    Linux*)     export LD_LIBRARY_PATH=$(rustc --print sysroot)/lib;;
    Darwin*)    export DYLD_LIBRARY_PATH=$(rustc --print sysroot)/lib;;
    *) exit 1
esac

# Allow rustfmt to use "nighly" features. `comment_width` is one of those for example.
# 0.2.17 started enforcing setting this variable to allow using the nighly features.
export CFG_RELEASE_CHANNEL=nightly

function correct_rustfmt() {
    if ! which $CMD; then
        echo "$CMD is not installed" >&2
        return 1
    fi
    local installed_version=$($CMD --version | cut -d'-' -f1)
    if [[ "$installed_version" != "$VERSION" ]]; then
        echo "Wrong version of $CMD installed. Expected $VERSION, got $installed_version" >&2
        return 1
    fi
    return 0
}

if [[ "${1:-""}" != "--only-format" ]]; then
    if ! correct_rustfmt; then
        echo "Installing $CMD $VERSION"
        $INSTALL_CMD
    fi
else
    shift
fi

find . -iname "*.rs" -not -path "*/target/*" -print0 | xargs -0 -n1 rustfmt --skip-children "$@"
