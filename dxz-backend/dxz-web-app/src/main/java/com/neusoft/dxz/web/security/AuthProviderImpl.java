package com.neusoft.dxz.web.security;

import com.neusoft.dxz.module.system.demo.enums.RoleType;
import com.google.common.base.Charsets;
import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.io.LineProcessor;
import com.google.common.io.Resources;
import lombok.ToString;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;

@Component
public class AuthProviderImpl implements AuthProvider {

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

        Yaml yaml = new Yaml();
        Auths auths = yaml.loadAs(Resources.toString(Resources.getResource("config/authorize.yaml"), Charsets.UTF_8), Auths.class);
        for (Auth auth : auths.auths) {
            Pattern urlPattern = Pattern.compile("^" + auth.url + "$");
            Set<Integer> types = Sets.newHashSet();
            if (auth.types != null && !auth.types.isEmpty()) {
                for (String type : auth.types) {
                    if (Objects.equal("ALL", type.toUpperCase())) {
                        //if all, means everyone(if login in)  can access this url
                        for (RoleType r : RoleType.values()) {
                            types.add(r.value());
                        }
                    } else {
                        types.add(RoleType.from(type).value());
                    }
                }
            }

            Set<String> roles = Sets.newHashSet();
            if(auth.roles!=null &&!auth.roles.isEmpty()){
                roles.addAll(auth.roles);
            }

            AuthItem authItem = new AuthItem(urlPattern, types, roles);
            protectList.add(authItem);
        }

        return protectList;
    }

    @ToString
    private static class Auth {
        public String url;

        public List<String> types;

        public List<String> roles;
    }

    @ToString
    private static class Auths {
        public List<Auth> auths;
    }
}
