package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.entity.Thing;
import com.gk.study.entity.ThingTag;
import com.gk.study.mapper.ThingMapper;
import com.gk.study.mapper.ThingTagMapper;
import com.gk.study.service.ThingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ThingServiceImpl extends ServiceImpl<ThingMapper, Thing> implements ThingService {
    @Autowired
    ThingMapper mapper;

    @Autowired
    ThingTagMapper thingTagMapper;

    @Override
    public List<Thing> getThingList(String keyword, String sort, String c, String tag) {
        QueryWrapper<Thing> queryWrapper = new QueryWrapper<>();

        //
        queryWrapper.like(StringUtils.isNotBlank(keyword), "title", keyword);

        //
        if (StringUtils.isNotBlank(sort)) {
            if (sort.equals("recent")) {
                queryWrapper.orderBy(true, false, "create_time");
            } else if (sort.equals("hot") || sort.equals("recommend")) {
                queryWrapper.orderBy(true, false, "pv");
            }
        } else {
            queryWrapper.orderBy(true, false, "create_time");
        }

        //
        if (StringUtils.isNotBlank(c) && !c.equals("-1")) {
            queryWrapper.eq(true, "classification_id", c);
        }

        List<Thing> things = mapper.selectList(queryWrapper);

        //
        if (StringUtils.isNotBlank(tag)) {
            List<Thing> tThings = new ArrayList<>();
            QueryWrapper<ThingTag> thingTagQueryWrapper = new QueryWrapper<>();
            thingTagQueryWrapper.eq("tag_id", tag);
            List<ThingTag> thingTagList = thingTagMapper.selectList(thingTagQueryWrapper);
            for (Thing thing : things) {
                for (ThingTag thingTag : thingTagList) {
                    if (thing.getId().equals(thingTag.getThingId())) {
                        tThings.add(thing);
                    }
                }
            }
            things.clear();
            things.addAll(tThings);
        }

        //
        for (Thing thing : things) {
            QueryWrapper<ThingTag> thingTagQueryWrapper = new QueryWrapper<>();
            thingTagQueryWrapper.lambda().eq(ThingTag::getThingId, thing.getId());
            List<ThingTag> thingTags = thingTagMapper.selectList(thingTagQueryWrapper);
            List<Long> tags = thingTags.stream().map(ThingTag::getTagId).collect(Collectors.toList());
            thing.setTags(tags);
        }
        return things;
    }

    @Override
    public void createThing(Thing thing) {
        System.out.println(thing);
        thing.setCreateTime(String.valueOf(System.currentTimeMillis()));

        if (thing.getPv() == null) {
            thing.setPv("0");
        }
        if (thing.getScore() == null) {
            thing.setScore("0");
        }
        if (thing.getWishCount() == null) {
            thing.setWishCount("0");
        }
        mapper.insert(thing);
        //
        setThingTags(thing);
    }

    @Override
    public void deleteThing(String id) {
        mapper.deleteById(id);
    }

    @Override
    public void updateThing(Thing thing) {
        //
        setThingTags(thing);
        mapper.updateById(thing);
    }

    @Override
    public Thing getThingById(String id) {
        Thing thing = mapper.selectById(id);
        thing.setPv(String.valueOf(Integer.parseInt(thing.getPv()) + 1));
        mapper.updateById(thing);

        return thing;
    }

    //
    @Override
    public void addWishCount(String thingId) {
        Thing thing = mapper.selectById(thingId);
        thing.setWishCount(String.valueOf(Integer.parseInt(thing.getWishCount()) + 1));
        mapper.updateById(thing);
    }

    //
    @Override
    public void addCollectCount(String thingId) {
        Thing thing = mapper.selectById(thingId);
        thing.setCollectCount(String.valueOf(Integer.parseInt(thing.getCollectCount()) + 1));
        mapper.updateById(thing);
    }

    //
    @Override
    public void subWishCount(String thingId) {
        Thing thing = mapper.selectById(thingId);
        int current = Integer.parseInt(thing.getWishCount());
        thing.setWishCount(String.valueOf(Math.max(0, current - 1)));
        mapper.updateById(thing);
    }

    //
    @Override
    public void subCollectCount(String thingId) {
        Thing thing = mapper.selectById(thingId);
        int current = Integer.parseInt(thing.getCollectCount());
        thing.setCollectCount(String.valueOf(Math.max(0, current - 1)));
        mapper.updateById(thing);
    }

    @Override
    public List<Thing> getUserThing(String userId) {
        QueryWrapper<Thing> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return mapper.selectList(queryWrapper);
    }
    
    @Override
    public void incrementPv(String id) {
        Thing thing = mapper.selectById(id);
        if (thing != null) {
            int currentPv = thing.getPv() != null ? Integer.parseInt(thing.getPv()) : 0;
            thing.setPv(String.valueOf(currentPv + 1));
            mapper.updateById(thing);
        }
    }

    public void setThingTags(Thing thing) {
        //
        Map<String, Object> map = new HashMap<>();
        map.put("thing_id", thing.getId());
        thingTagMapper.deleteByMap(map);
        //
        if (thing.getTags() != null) {
            for (Long tag : thing.getTags()) {
                ThingTag thingTag = new ThingTag();
                thingTag.setThingId(thing.getId());
                thingTag.setTagId(tag);
                thingTagMapper.insert(thingTag);
            }
        }
    }

}










