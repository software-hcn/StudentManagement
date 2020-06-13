package com.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String success_path="studentdata.jsp";
		String fail_path="login.jsp";
		String userid=request.getParameter("userid");
		String userpass = request.getParameter("userpass");
		List<String> info = new ArrayList<>();
		if(userid==null || "".equals(userid)){
			info.add("�û�ID����Ϊ��");
			request.setAttribute("info", info);
			request.getRequestDispatcher(fail_path).forward(request, response);
		}
		if(userpass == null || "".equals(userpass)){
			info.add("���벻��Ϊ��");
			request.setAttribute("info", info);
			request.getRequestDispatcher(fail_path).forward(request, response);
		}
		if(info.size()==0){
			Userstudent user = new Userstudent();
			user.setUserid(userid);
			user.setPassword(userpass);
			try {
				if(DAOFactory.getIUserInstance().findLogin(user)){
					info.add("�û���¼�ɹ�����ӭ��ĵ��� "+ user.getName());
					request.setAttribute("info", info);
					request.getRequestDispatcher(success_path).forward(request, response);
				}else{
					info.add("�û���¼ʧ�ܣ����������û���������");
					request.setAttribute("info", info);
					request.getRequestDispatcher(fail_path).forward(request, response);
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			
		}
	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
