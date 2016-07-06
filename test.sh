#!/bin/sh

current_context=$(kubectl config current-context view)

kubectl config use-context test

kubectl delete deployment test
kubectl delete svc myservice

gradle test

kubectl config use-context ${current_context}
