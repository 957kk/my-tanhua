package com.tanhua.dubbo.server.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Builder
//@Setter
//@Getter
public class Item implements Serializable {
    private static final long serialVersionUID = 362498820763181265L;

    private Long id;
    private String title;
    private Date created;

    public static void main(String[] args) {
        Item item = new Item();
        item.setId(111L);
        item.setTitle("abc");

//        System.out.println(item);

        log.info("item : " + item.toString());

        Item item2 = Item.builder().id(222L).title("qaz").build();
        System.out.println(item2);
    }

}
