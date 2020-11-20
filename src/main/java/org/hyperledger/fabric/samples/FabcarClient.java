/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples;

import java.util.Objects;
import java.nio.file.Path;
import com.google.protobuf.ByteString;

import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.protos.peer.ProposalResponsePackage;
import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.Wallet;

public final class FabcarClient {

    private FabcarClient() {
    }

    public static void main(final String[] args) throws Exception {
        CLI cli = new CLI(args);

        Wallet wallet = cli.getWallet();

        Path networkConfigPath = cli.getConnectionProfilePath();

        String identity = cli.getIdentity();
        Gateway.Builder builder = Gateway.createBuilder();
        builder.identity(wallet, identity).networkConfig(networkConfigPath).discovery(true);

        // create a gateway connection
        try (Gateway gateway = builder.connect()) {

            // get the network and contract
            Network network = gateway.getNetwork("mychannel");
            Contract contract = network.getContract("fabcar");

            byte[] result;

            result = contract.evaluateTransaction("queryAllCars");
            System.out.println(new String(result));

            contract.submitTransaction("createCar", "CAR10", "VW", "Polo", "Grey", "Mary");

            result = contract.evaluateTransaction("queryCar", "CAR10");
            System.out.println(new String(result));

            contract.submitTransaction("changeCarOwner", "CAR10", "Archie");

            result = contract.evaluateTransaction("queryCar", "CAR999");
            System.out.println(new String(result));
        } catch (ContractException e) {
            byte[] payload = e.getProposalResponses().stream()
                .map(ProposalResponse::getProposalResponse)
                .map(ProposalResponsePackage.ProposalResponse::getPayload)
                .filter(Objects::nonNull)
                .findFirst()
                .map(ByteString::toByteArray)
                .orElse(null);
            System.out.println("MESSAGE " + e.getMessage());
            System.out.println("PAYLOAD " + new String(payload));
        }
    }
}
