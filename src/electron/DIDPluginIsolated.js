let fs = require("fs")
const { contextBridge, ipcRenderer, remote } = require("electron")
require("../../../../trinity-renderer")

contextBridge.exposeInMainWorld(
    'didManagerImpl',
    TrinityRenderer.TrinityRuntimeHelper.createIPCDefinitionToMainProcess("DIDPlugin", [
        "getVersion",
        "setListener",
        "initDidStore",
        "deleteDidStore",
        "CreateDIDDocumentFromJson",
        "generateMnemonic",
        "isMnemonicValid",
        "DIDManager_resolveDIDDocument",
        "DIDStore_changePassword",
        "containsPrivateIdentity",
        "initPrivateIdentity",
        "exportMnemonic",
        "setResolverUrl",
        "synchronize",
        "deleteDid",
        "newDid",
        "listDids",
        "loadDid",
        "publishDid",
        "resolveDid",
        "storeDid",
        "CreateCredential",
        "deleteCredential",
        "DID_loadCredentials",
        "loadCredential",
        "storeCredential",
        "getDefaultPublicKey",
        "DIDDocument_addService",
        "DIDDocument_removeService",
        "DIDDocument_toJson",
        "addCredential",
        "DIDDocument_deleteCredential",
        "DIDDocument_getCredentials",
        "sign",
        "verify",
        "signDigest",
        "createJWT",
        "getMethod",
        "getMethodSpecificId",
        "prepareIssuer",
        "getController",
        "getPublicKeyBase58",
        "createVerifiablePresentationFromCredentials",
        "verifiablePresentationIsValid",
        "verifiablePresentationIsGenuine",
        "verifiablePresentationToJson",
        "DIDManager_parseJWT"
    ])
)
