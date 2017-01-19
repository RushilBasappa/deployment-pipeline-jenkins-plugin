#!/bin/bash


function render_template() {
  contents=$(cat $1)
  tmpl=$(echo $contents |sed -e 's/%%\(.*\)%%/${\1}/g')
  eval "echo $tmpl"
}

template="random string"
render_template somescript.sh
