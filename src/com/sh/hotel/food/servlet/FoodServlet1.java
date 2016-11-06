package com.sh.hotel.food.servlet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import cn.itcast.commons.CommonUtils;

import com.sh.hotel.food.domain.Food;
import com.sh.hotel.food.service.impl.FoodService;
import com.sh.hotel.foodtype.domain.FoodType;
import com.sh.hotel.foodtype.service.impl.FoodTypeService;


public class FoodServlet1 extends HttpServlet {

	private static Logger log = Logger.getLogger(FoodServlet1.class.toString());
	private FoodService foodService = new FoodService();
	private FoodTypeService foodTypeService = new FoodTypeService();
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		String method = request.getParameter("method");
		if("addFood".equals(method)) {
			addFood(request, response);
		} else if("updateFood".equals(method)) {
			updateFood(request, response);
		} else if("deleteFood".equals(method)) {
			deleteFood(request, response);
		} else if("findAllFood".equals(method)) {
			
			findAllFood(request, response);
		} else if("addFood_findAllFoodType".equals(method)) {
			
			addFood_findAllFoodType(request, response);
		} else if("updateFood_findFoodById".equals(method)) {
			updateFood_findFoodById(request, response);
		}
		
	}

	/**
	 * 根据id查找对于的菜品及所在的菜系
	 * 以及查找所以的菜系
	 * @param request
	 * @param response
	 */
	private void updateFood_findFoodById(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		/**
		 * 查找菜品和菜系
		 */
		String fId = request.getParameter("fId");
		//log.info(fId + "----");
		Map<String, Object> map = foodService.findFoodById(Integer.parseInt(fId));
		Food food = CommonUtils.toBean(map, Food.class);
		FoodType foodType = CommonUtils.toBean(map, FoodType.class);
		food.setFoodType(foodType);
		request.setAttribute("food", food);
		/**
		 * 查找所以菜系
		 */
		List<FoodType> foodTypeList = foodTypeService.findAll();
		request.setAttribute("foodtypeList", foodTypeList);
		String path = "/sys/food/updateFood.jsp";
		try {
			request.getRequestDispatcher(path).forward(request, response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
		
		
		
		
	}


	private void addFood_findAllFoodType(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		List<FoodType> foodTypeList = foodTypeService.findAll();
		request.setAttribute("foodtypeList", foodTypeList);
		String path = "/sys/food/saveFood.jsp";
		try {
			request.getRequestDispatcher(path).forward(request, response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
	}


	private void findAllFood(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		List<Map<String, Object>> maps = foodService.findAll();
		List<Food> foodsList = new ArrayList<Food>();
		for (Map<String, Object> map : maps) {
			Food food = CommonUtils.toBean(map, Food.class);
			FoodType foodType = CommonUtils.toBean(map, FoodType.class);
			food.setFoodType(foodType);
			foodsList.add(food);
		}
		
		
		request.setAttribute("foodsList", foodsList);
		String path = "/sys/food/foodList.jsp";
		try {
			request.getRequestDispatcher(path).forward(request, response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
	}


	/**
	 * 需要上传文件，和普通表单不一样
	 * 上传三步走
	 * @param request
	 * @param response
	 */
	private void addFood(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload sfu = new ServletFileUpload(factory);
		if(sfu.isMultipartContent(request)){
			try {
				List<FileItem> list = sfu.parseRequest(request);
				Map<String, Object> map = new HashMap<String, Object>();
				for (FileItem fileItem : list) {
					if(fileItem.isFormField()){
						//如果是普通文本类型
						String name = fileItem.getFieldName();
						String value = fileItem.getString("utf-8");//处理文件中文编码问题
						map.put(name, value);
						
					} else {
						//如果是文件类型
						String fileName = fileItem.getName();//得到文件名称
						String name = fileItem.getFieldName();
						log.info("name:" + name);
						String path = this.getServletContext().getRealPath("/upload");
						File file = new File(path, fileName);
						fileItem.write(file);
						fileItem.delete();
						map.put(name, fileName);
						
						
					}
				}
				Food food = CommonUtils.toBean(map, Food.class);
				foodService.addFood(food);
				log.info("添加菜品: " + food.toString());
				findAllFood(request, response);
			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("不是上传文件类型");
		}
		
		
		
		
		
	}


	/**
	 * 依然使用上传文件
	 * 使用第三方库common-fileupload
	 * 上传三步走
	 * 1、解析工厂
	 * 2.解析器
	 * 3、解析request
	 * @param request
	 * @param response
	 */
	private void updateFood(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload sfu = new ServletFileUpload(factory);
		if(sfu.isMultipartContent(request)){
			try {
				List<FileItem> fileItems = sfu.parseRequest(request);
				Map<String, Object> map = new HashMap<String, Object>();
				for (FileItem fileItem : fileItems) {
					if(fileItem.isFormField()) {
						//普通表单文件
						String name = fileItem.getFieldName();
						String value = fileItem.getString("utf-8");
						map.put(name, value);
					} else {
						//文件表单
						String fileName = fileItem.getName();
						String name = fileItem.getFieldName();
						String path = this.getServletContext().getRealPath("/upload");
						File file = new File(path, fileName);
						fileItem.write(file);
						fileItem.delete();
						map.put(name, fileName);
						//log.info("name" + name);
					}
				}
				Food food = CommonUtils.toBean(map, Food.class);
				log.info(food.toString());
				foodService.updateFood(food);
				findAllFood(request, response);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("不是表单文件");
		}
		
		
	}


	private void deleteFood(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		String fId = request.getParameter("fId");
		
		foodService.deleteFood(Integer.parseInt(fId));
		findAllFood(request, response);
		
	}


	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		this.doGet(request, response);
	}

}
