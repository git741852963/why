package com.neusoft.features.db.mybatis.type;

import com.neusoft.features.common.utils.JsonMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * JSON Type Handler
 *
 * @author andy.jiao@msn.com
 */
public class JsonTypeHandler<T extends Object> extends BaseTypeHandler<T> {

    private Class<T> clazz;

    public JsonTypeHandler(Class<T> clazz) {
        if (clazz == null) throw new IllegalArgumentException("Type argument cannot be null");
        this.clazz = clazz;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps,
                                    int i,
                                    Object parameter,
                                    JdbcType jdbcType) throws SQLException {
        ps.setString(i, JsonMapper.JSON_NON_EMPTY_MAPPER.toJson(parameter));
    }



    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return JsonMapper.JSON_NON_EMPTY_MAPPER.fromJson(rs.getString(columnName), clazz);

    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return JsonMapper.JSON_NON_EMPTY_MAPPER.fromJson(rs.getString(columnIndex), clazz);
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return JsonMapper.JSON_NON_EMPTY_MAPPER.fromJson(cs.getString(columnIndex), clazz);
    }
}

//        import JsonMapper;
//        import org.apache.ibatis.type.BaseTypeHandler;
//        import org.apache.ibatis.type.JdbcType;
//
//        import java.sql.CallableStatement;
//        import java.sql.PreparedStatement;
//        import java.sql.ResultSet;
//        import java.sql.SQLException;
//
///**
// * JSON Type Handler
// *
// * @author andy.jiao@msn.com
// */
////@MappedTypes(VideoSource.class)
//public class JsonTypeHandler extends BaseTypeHandler<Object> {
//    @Override
//    public void setNonNullParameter(PreparedStatement ps,
//                                    int i,
//                                    Object parameter,
//                                    JdbcType jdbcType) throws SQLException {
//        ps.setString(i, JsonMapper.JSON_NON_EMPTY_MAPPER.toJson(parameter));
//    }
//
//
//
//    @Override
//    public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
//        return JsonMapper.JSON_NON_EMPTY_MAPPER.fromJson(rs.getString(columnName), Object.class);
//
//    }
//
//    @Override
//    public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
//        return JsonMapper.JSON_NON_EMPTY_MAPPER.fromJson(rs.getString(columnIndex), Object.class);
//    }
//
//    @Override
//    public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
//        return JsonMapper.JSON_NON_EMPTY_MAPPER.fromJson(cs.getString(columnIndex), Object.class);
//    }
//}
