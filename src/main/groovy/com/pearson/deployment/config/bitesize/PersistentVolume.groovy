package com.pearson.deployment.config.bitesize

// volumes:
//   - name: nfs
//     mount: /nfs
//     mode: ReadWriteOnce (ReadOnlyMany,ReadWriteMany)
//     size: 100G

class PersistentVolume extends ManagedResource implements Serializable {
  String namespace
  String path
  String modes
  String size

  String getSize() {
    return this.size
  }
}