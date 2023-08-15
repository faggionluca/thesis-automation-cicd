package com.lucafaggion.thesis.common.model.UserAssociated.bitbucket;

import com.lucafaggion.thesis.common.model.UserAssociatedAccount;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class BitbucketAccount  extends UserAssociatedAccount{
    public String type;
    public String created_on;
    public String display_name;
    public String uuid;
}