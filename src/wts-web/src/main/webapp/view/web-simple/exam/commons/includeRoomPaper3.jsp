<%@ page language="java" pageEncoding="utf-8"%>
<%@page import="com.farm.web.constant.FarmConstant"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/view/conf/farmtag.tld" prefix="PF"%>
<!-- 随机答题试卷 -->
<div class="col-md-6" style="padding-left: 8px; padding-right: 8px;">
	<div class="panel panel-default">
		<div class="panel-body">
			<div class="media" style="height: 153px; overflow: hidden;">
				<div
					style="text-align: center; border-bottom: 1px dashed #ccc; margin-bottom: 8px; padding-bottom: 0px;">
					<div class="doc_node_title_box"
						style="font-size: 16px; overflow: hidden; white-space: nowrap; text-overflow: ellipsis;">
						<c:if test="${room.room.pshowtype=='3'}">
							<code style="float: left;">练习</code>
						</c:if>
						<c:if test="${room.room.pshowtype=='4'}">
							<code style="float: left;">学习</code>
						</c:if>
						&nbsp;
						<c:if test="${empty paper.roomPaper.name}">${paper.info.name}</c:if>
						<c:if test="${not empty paper.roomPaper.name}">${paper.roomPaper.name}</c:if>
					</div>
				</div>
				<div class="pull-right">
					<img alt="答题室" style="width: 64px; height: 64px;"
						src="text/img/random.png">
				</div>
				<div class="media-body">
					<div style="margin-left: 4px; height: 56px; overflow: hidden;"
						class="pull-left">
						<div class="side_unit_info"
							style="max-height: 38px; overflow: hidden;">
							<b>题量</b>：共${paper.subjectNum}道题
						</div>
						<div style="margin-left: -6px;">
							<%@ include
								file="/view/web-simple/exam/commons/includeRoomMenus.jsp"%>
						</div>
					</div>
				</div>
				<div style="padding-top: 20px;">
					<div class="btn-group btn-group-justified" role="group"
						aria-label="...">
						<div class="btn-group" role="group">
							<a data-toggle="modal" data-target="#${paper.info.id}-win"
								type="button" class="btn btn-success">开始&nbsp;Go!</a>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<!-- 开始答题-->
<div class="modal fade" id="${paper.info.id}-win" tabindex="-1"
	role="dialog" aria-labelledby="myModalLabel">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title" id="myModalLabel">《${paper.info.name}》</h4>
			</div>
			<div class="modal-body">
				<div class="doc_node_title_box" style="font-size: 16px;">确认是否开始?</div>
				<c:if test="${paper.room.writetype!='2'}">
					<div class="doc_node_title_box"
						style="font-size: 16px; color: #d13133;">当前人员：${USEROBJ.name}</div>
				</c:if>
				<c:if test="${paper.room.writetype=='2'}">
					<div class="doc_node_title_box"
						style="font-size: 16px; color: #d13133;">当前人员：匿名</div>
				</c:if>
				<c:if test="${room.room.pshowtype=='3'}">
					<div class="side_unit_info" style="font-size: 14px;">
						共<b>${paper.subjectNum}</b>道题,系统将按照随机顺序依次出题,可随时终止答题。
					</div>
				</c:if>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
				<c:if test="${room.room.pshowtype=='3'}">
					<a
						href="websubject/PubRandomSubject.do?paperid=${paper.info.id}&roomid=${room.room.id}"
						class="btn btn-primary">立即开始Go!</a>
				</c:if>
				<c:if test="${room.room.pshowtype=='4'}">
					<a
						href="webpaper/Publearnpage.do?paperid=${paper.info.id}&roomid=${room.room.id}"
						class="btn btn-primary">立即开始Go!</a>
				</c:if>
			</div>
		</div>
	</div>
</div>