package com.flaze.rewards.mapper;

import com.flaze.rewards.dto.RewardsDTO;
import com.flaze.rewards.schema.Rewards;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public final class RewardsMapper {

    public static List<RewardsDTO> toDTOList(List<Rewards> rewardsList){
        return rewardsList.stream().map(RewardsMapper::toRewardsDTO).collect(Collectors.toList());
    }


    public static RewardsDTO toRewardsDTO(Rewards rewards) {
        return RewardsDTO.builder()
                .customerId(rewards.getRewardsPK().getCustomerId())
                .transactionId(rewards.getRewardsPK().getTransactionId())
                .amount(rewards.getAmount())
                .points(rewards.getPoints())
                .transactionDate(rewards.getTransactionDate())
                .build();
    }

}
