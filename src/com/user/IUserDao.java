package com.user;

public interface IUserDao {
	/**
	 * 	
	 * @param user		����VO����
	 * @return				��֤�������
	 * @throws Exception		�׳��쳣
	 */
	public boolean findLogin(Userstudent user) throws Exception;

	public void update(Userstudent user);
}
