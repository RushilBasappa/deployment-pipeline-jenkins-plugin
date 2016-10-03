package com.pearson.deployment.config.bitesize

// volumes:
//   - name: nfs
//     mount: /nfs
//     mode: ReadWriteOnce (ReadOnlyMany,ReadWriteMany)
//     size: 100G

class PersistentVolume extends ManagedResource implements Serializable {
  String namespace
  String path
  String mode
  String size


  // def podMountResource() {
  //   [
  //     "mountPath": path,
  //     "name": name
  //   ]

  // }

  // def podVolumeResource() {
  //   [
  //     "name": name,
  //     "persistentVolumeClaim": [
  //       "claimName": name
  //     ]
  //   ]
  // }

  // def volumeClaimResource() {
  //   [
  //     "kind": "PersistentVolumeClaim",
  //     "apiVersion": "v1",
  //     "metadata": [
  //       "name": name,
  //       "namespace": namespace
  //     ],
  //     "spec": [
  //       "accessModes": mode.split(','),
  //       "resources": [
  //         "requests": [
  //           "storage": size
  //         ]
  //       ]
  //     ]
  //   ]
  // }
}