package com.fuzhu.studentmanager;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.user.Slf4jTest;

public class DAO {
	  private static Logger Log = LoggerFactory.getLogger(Slf4jTest.class);

	// INSERT, UPDATE, DELETE���������԰���������
		public void update(String sql, Object... args) {
			Connection connection = null;
			PreparedStatement preparedStatement = null;
			try {
				connection = JDBCTools.getConnection();
				preparedStatement = connection.prepareStatement(sql);
				for (int i = 0; i < args.length; i++) {
					preparedStatement.setObject(i + 1, args[i]);
				}
				preparedStatement.executeUpdate();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				JDBCTools.release(null, preparedStatement, connection);
			}
		}
		
	public <T> T get(Class<T> clazz, String sql, Object... args) {
		List<T> result = getForList(clazz, sql, args);
		if (result.size() > 0) {
			return result.get(0);
		}
		return null;
	}
	
	// ��ѯ������¼�����ض�Ӧ�Ķ���ļ���
		public <T> List<T> getForList(Class<T> clazz, String sql, Object... args) {
			List<T> list = new ArrayList<>();

			Connection connection = null;
			PreparedStatement preparedStatement = null;
			ResultSet resultSet = null;
			try {
				// �ܲ���.��1�� �õ������
				connection = JDBCTools.getConnection();
				preparedStatement = connection.prepareStatement(sql);
				for (int i = 0; i < args.length; i++) {
					preparedStatement.setObject(i + 1, args[i]);
				}
				resultSet = preparedStatement.executeQuery();
				// �ܲ��裨2�������������õ�Map��List������һ��Map�������һ����¼��Map��keyΪresultSet���еı�����Map��valueΪ�е�ֵ
				// 5-11���������ȡ�������������õ�Map��һ��List������һ��Map�����Ӧһ����¼
				List<Map<String, Object>> values = handleResultSetToMapList(resultSet);

				// �ܲ��裨3����Map��ListתΪclazz��Ӧ��List���ϣ�����Map��key��Ϊclazz��Ӧ�Ķ����propertyName����Map��value��Ϊclazz��Ӧ�Ķ����propertyValue
				// 12.
				// �ж�List�Ƿ�Ϊ�գ���Ϊ�������List���õ�һ��һ����Map���ٰ�һ��Map����תΪһ����Ӧ��Class������Ӧ��Object����
				list = transferMapListToBeanList(clazz, values);

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			} finally {
				JDBCTools.release(resultSet, preparedStatement, connection);
			}
			return list;
		}
		/**
		 * �����������õ�Map��һ��List������һ��Map�����Ӧһ����¼
		 * 
		 * @param resultSet
		 * @return
		 * @throws SQLException
		 */
		private List<Map<String, Object>> handleResultSetToMapList(ResultSet resultSet) throws SQLException {
			// 5.
			// ��ResultSet���м�¼--׼��һ��List<Map<String,Object>>����������еı�����ֵ������е�ֵ������һ��Map�����Ӧһ����¼��
			List<Map<String, Object>> values = new ArrayList<>();

			// ResultSetMetaData rsmd =resultSet.getMetaData(); //�Ż��������
			List<String> columnLabels = getColumnLabels(resultSet);
			Map<String, Object> map = null;
			// 7. ����ResultSet����while����������
			while (resultSet.next()) {
				map = new HashMap<>();
				// 8.��ResultSetMetaData����õ���������ж����С����������С�(���Ż�����)
				// for(int i=0;i<rsmd.getColumnCount();i++){
				// String columnLabel = rsmd.getColumnLabel(i+1);
				// Object value= resultSet.getObject(i+1);
				// map.put(columnLabel, value);
				// }
				for (int i = 0; i < columnLabels.size(); i++) {
					// �Ż���
					String columnLabel = columnLabels.get(i);
					Object value = resultSet.getObject(columnLabel); // ���Ը���������ȡ��Ҳ���Ը���������ȡ
					map.put(columnLabel, value);
				}
				// 11.��һ����¼��һ��Map�������5׼����List��
				values.add(map);
			}
			return values;
		}
		
		/**
		 * ��Map��ListתΪclazz��Ӧ��List���ϣ�����Map��key��Ϊclazz��Ӧ�Ķ����propertyName��
		 * ��Map��value��Ϊclazz��Ӧ�Ķ����propertyValue
		 * 
		 * @param clazz
		 * @param values
		 * @return
		 * @throws InstantiationException
		 * @throws IllegalAccessException
		 * @throws InvocationTargetException
		 */
		private <T> List<T> transferMapListToBeanList(Class<T> clazz, List<Map<String, Object>> values)
				throws InstantiationException, IllegalAccessException, InvocationTargetException {
			List<T> result = new ArrayList<>();
			T bean = null;
			if (values.size() > 0) {
				for (Map<String, Object> m : values) {
					for (Map.Entry<String, Object> entry : m.entrySet()) {
						String propertyName = entry.getKey();
						Object value = entry.getValue();
						bean = clazz.newInstance();
						BeanUtils.setProperty(bean, propertyName, value);
					}
					// 13.��Object������뵽list�С�
					// ����Map�����÷��������������ֵ��������ΪMap�е�key������ֵΪMap�е�Value
					result.add(bean);
				}
			}
			return result;
		}
		// �Ż�������
		/**
		 * ��ȡ�������ColumnLabel ��Ӧ��list
		 * 
		 * @param rs
		 * @return
		 * @throws SQLException
		 */
		private List<String> getColumnLabels(ResultSet rs) throws SQLException {
			List<String> labels = new ArrayList<>();
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 0; i < rsmd.getColumnCount(); i++) {
				labels.add(rsmd.getColumnLabel(i + 1));
			}
			return labels;
		}

}
