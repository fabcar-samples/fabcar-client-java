/*
SPDX-License-Identifier: Apache-2.0
*/

package org.hyperledger.fabric.samples;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.hyperledger.fabric.gateway.Identities;
import org.hyperledger.fabric.gateway.Identity;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class CLI {
    private static final int MIN_ARGS = 3;
    private static final int MAX_ARGS = 4;
    private static final int PROFILE_ARG = 0;
    private static final int IDENTITY_ARG = 1;
    private static final int WALLET_ARG = 2;
    private static final int CERTIFICATE_ARG = 2;
    private static final int PRIVATE_KEY_ARG = 3;

    private final Path profilePath;
    private final String identity;
    private final String mspId;
    private final Wallet wallet;

    public CLI(final String[] args) {
        if ((args.length < MIN_ARGS) || (args.length > MAX_ARGS)) {
            throw new IllegalArgumentException(
                    "Usage:\n\tjava simpleFabricClient.jar connectionProfile identity certificate privateKey\n"
                            + "\tjava simpleFabricClient.jar connectionProfile identity wallet");
        }

        this.profilePath = initProfilePath(args[PROFILE_ARG]);
        this.identity = args[IDENTITY_ARG];
        this.mspId = initMspId();

        if (args.length == MIN_ARGS) {
            this.wallet = initWallet(args[WALLET_ARG]);
        } else {
            this.wallet = initWallet(args[CERTIFICATE_ARG], args[PRIVATE_KEY_ARG]);
        }
    }

    public Path getConnectionProfilePath() {
        return this.profilePath;
    }

    public String getIdentity() {
        return this.identity;
    }

    public String getMspId() {
        return this.mspId;
    }

    public Wallet getWallet() {
        return this.wallet;
    }

    private Path initProfilePath(final String profileArg) {
        Path result = Paths.get(profileArg).toAbsolutePath();
        if (!Files.exists(result)) {
            String message = String.format("Connection profile does not exist: %s", result.toString());
            throw new IllegalArgumentException(message);
        }

        return result;
    }

    private String initMspId() {
        try {
            byte[] jsonData = Files.readAllBytes(this.profilePath);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode profileObj = mapper.readTree(jsonData);

            String orgName = profileObj.path("client").path("organization").asText();
            String result = profileObj.path("organizations").path(orgName).path("mspid").asText();

            if (result.equals("")) {
                String message = String.format("Could not find mspid in connection profile: %s",
                        profilePath.toString());
                throw new IllegalArgumentException(message);
            }

            return result;
        } catch (IOException e) {
            String message = String.format("Could not read connection profile: %s", profilePath.toString());
            throw new IllegalArgumentException(message, e);
        }
    }

    private Wallet initWallet(final String walletArg) {
        Path walletPath = Paths.get(walletArg).toAbsolutePath();

        try {
            Wallet result = Wallets.newFileSystemWallet(walletPath);

            return result;
        } catch (IOException e) {
            String message = String.format("Could not open wallet: %s", walletPath.toString());
            throw new RuntimeException(message, e);
        }
    }

    private Wallet initWallet(final String certificateArg, final String privateKeyArg) {
        Path certificatePath = Paths.get(certificateArg).toAbsolutePath();
        Path privateKeyPath = Paths.get(privateKeyArg).toAbsolutePath();
        Reader certificateReader = null;
        Reader privateKeyReader = null;

        try {
            certificateReader = new FileReader(certificatePath.toFile());
            privateKeyReader = new FileReader(privateKeyPath.toFile());

            X509Certificate certificate = Identities.readX509Certificate(certificateReader);
            PrivateKey privateKey = Identities.readPrivateKey(privateKeyReader);
            Identity id = Identities.newX509Identity(this.mspId, certificate, privateKey);

            Wallet result = Wallets.newInMemoryWallet();
            result.put(this.identity, id);

            return result;
        } catch (IOException | CertificateException | InvalidKeyException e) {
            String message = String.format(
                    "Could not create wallet for %s using the specified certificate and private key files: %s %s",
                    this.identity, certificatePath.toString(), privateKeyPath.toString());
            throw new RuntimeException(message, e);
        } finally {
            if (certificateReader != null) {
                try {
                    certificateReader.close();
                } catch (IOException e) {
                    // we tried
                }
            }

            if (privateKeyReader != null) {
                try {
                    privateKeyReader.close();
                } catch (IOException e) {
                    // we tried
                }
            }
        }
    }

}
