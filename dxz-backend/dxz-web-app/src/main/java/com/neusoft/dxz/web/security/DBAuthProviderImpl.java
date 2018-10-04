package com.neusoft.dxz.web.security;

import com.neusoft.dxz.module.system.demo.services.ResourceService;
import com.neusoft.features.api.enums.ControllerSuffix;
import com.neusoft.features.common.dto.NameValuePair;
import com.neusoft.features.common.model.Response;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.LineProcessor;
import com.google.common.io.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;

@Component
public class DBAuthProviderImpl implements AuthProvider {

    @Autowired
    private ResourceService resourceService;

    @Override
    public Set<WhiteItem> getWhiteList() throws IOException {
        final Set<WhiteItem> whiteList = Sets.newHashSet();
        Resources.readLines(Resources.getResource("config/white_list"), Charsets.UTF_8, new LineProcessor<Void>() {
            @Override
            public boolean processLine(String line) throws IOException {
                if (!Strings.isNullOrEmpty(line)) {
                    List<String> parts = Splitter.on(':').trimResults().splitToList(line);
                    checkState(parts.size() == 2, "illegal white_list configuration [%s]", line);
                    Pattern urlPattern = Pattern.compile("^" + parts.get(0) + "$");
                    String methods = parts.get(1).toLowerCase();
                    ImmutableSet.Builder<String> httpMethods = ImmutableSet.builder();
                    for (String method : Splitter.on(',').omitEmptyStrings().trimResults().split(methods)) {
                        httpMethods.add(method);
                    }
                    whiteList.add(new WhiteItem(urlPattern, httpMethods.build()));
                }
                return true;
            }

            @Override
            public Void getResult() {
                return null;
            }
        });

        return whiteList;
    }

    @Override
    public Set<AuthItem> getProtectList() throws IOException {
        Set<AuthItem> protectList = Sets.newHashSet();

        Response<List<NameValuePair>> result = resourceService.findAllResRoles();
        checkState(result.isSuccess(), result.getError());

        List<NameValuePair> res = result.getResult();
        if (res.size() > 0) {
            List<String> list = Lists.newArrayList();
            for(ControllerSuffix e : ControllerSuffix.values()) {
                list.add(e.value());
            }
            String suffix = Joiner.on("|").join(list);
            String regexSuffix = "((\\/[a-zA-Z0-9-_])+\\.(" + suffix + "))*$";
            for (NameValuePair pair : res) {
                // 正则表达式满足： 1.可以匹配url本身（页面） 2.可以匹配url之上的任意json/do/action等请求
                Pattern pattern = Pattern.compile("^" + pair.getName() + regexSuffix);
                Iterable<String> roles = Splitter.on(",").split(pair.getValue().toString());

                Set<String> roleSet = Sets.newHashSet();
                for (String role : roles) {
                    roleSet.add(role);
                }
                protectList.add(new AuthItem(pattern, null, roleSet));
            }
        }
        return protectList;
    }
}
