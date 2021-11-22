package controller;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import model.DAO;
import model.JavaBeans;

@WebServlet(urlPatterns = { "/Controller", "/main", "/insert", "/select", "/update", "/delete", "/report" })
public class Controller extends HttpServlet {
	private static final long serialVersionUID = 1L;
	DAO dao = new DAO();
	JavaBeans contato = new JavaBeans();

	public Controller() {
		super();

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String action = request.getServletPath();
		
		//Teste de recebimento de requisições
		//System.out.println(action);
		
		if (action.equals("/main")) {
			contatos(request, response);

		} else if (action.equals("/insert")) {

			adicionarContato(request, response);
		} else if (action.equals("/select")) {

			listarContato(request, response);
		} else if (action.equals("/update")) {

			editarContato(request, response);
		} else if (action.equals("/delete")) {

			removerContato(request, response);
		}else if (action.equals("/report")) {

			gerarRelatorio(request, response);
		}
		
		else {
			response.sendRedirect("index.html");
		}

	}

	// Listar contatos
	protected void contatos(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Criando um objeto que ira os dados javabeans
		ArrayList<JavaBeans> lista = dao.listarContatos();
		// teste de recebimento da lista;
		// for(int i = 0; i< lista.size(); i++) {
		// System.out.println(lista.get(i).getIdcon());
		// System.out.println(lista.get(i).getNome());
		// System.out.println(lista.get(i).getFone());
		// System.out.println(lista.get(i).getEmail());

		// Encaminhar a lista ao documento agenda.jsp
		request.setAttribute("contatos", lista);
		RequestDispatcher rd = request.getRequestDispatcher("agenda.jsp");
		rd.forward(request, response);
	}

	// Novo contatos
	protected void adicionarContato(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// ----------------------------------------------
		// teste de recebimento dos dados do formulário
		// System.out.println(request.getParameter("nome"));
		// System.out.println(request.getParameter("fone"));
		// System.out.println(request.getParameter("email"));
		// ------------------------------------------------

		// setar as variaveis do java beans
		contato.setNome(request.getParameter("nome"));
		contato.setFone(request.getParameter("fone"));
		contato.setEmail(request.getParameter("email"));

		// Invocar o método inserirContato passando o objeto contato
		dao.inserirContato(contato);
		// Redirecionar para o documento agenda.jsp
		response.sendRedirect("main");

	}

	// Editar contato
	protected void listarContato(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Recebimento do id do contato que será editado
		String idcon = request.getParameter("idcon");
		System.out.println(idcon);
		// setar a variavel JavaBeans
		contato.setIdcon(idcon);
		// Executar o metodi selecionarContato (DAO)
		dao.selecionarContato(contato);
		// setar os atributos do formualario com o conteudo Javabeans
		request.setAttribute("idcon", contato.getIdcon());
		request.setAttribute("nome", contato.getNome());
		request.setAttribute("fone", contato.getFone());
		request.setAttribute("email", contato.getEmail());
		// Encaminhar ao documetno editar.jsp
		RequestDispatcher rd = request.getRequestDispatcher("editar.jsp");
		rd.forward(request, response);

	}

	protected void editarContato(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Setar as variaveis JavaBeans
		contato.setIdcon(request.getParameter("idcon"));
		contato.setNome(request.getParameter("nome"));
		contato.setFone(request.getParameter("fone"));
		contato.setEmail(request.getParameter("email"));
		// executar o método alterarContato
		dao.alterarContato(contato);
		// Redirecionar para o documento agenda.jsp (atualizando as alterações
		response.sendRedirect("main");
	}

	// Remover o contato
	protected void removerContato(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Recebimento do contato a ser removido (validador.js)
		String idcon = request.getParameter("idcon");
		// Setar a variavel idcon no javabeans
		contato.setIdcon(idcon);
		// Executar o metodo deletarContato (DAO) passando o objeto contato
		dao.deletarContato(contato);
		// Redirecionar para o documento agenda.jsp (atualizando as alterações
		response.sendRedirect("main");

	}
	//Gerar relatorio em pdf
	protected void gerarRelatorio(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
			Document documento = new Document();
			try {
				//tipo de conteúdo
				response.setContentType("apllication/pdf");
				//nome do cdocumento
				response.addHeader("Content-Disposition","inline; filename=" + "contatos.pdf");
				//Criar o documento 
				PdfWriter.getInstance(documento, response.getOutputStream());
				//abri o documento -> conteudo
				documento.open();
				documento.add(new Paragraph("Lista de contatos:"));
				documento.add(new Paragraph(""));
				//Criar uma tabela
				PdfPTable tabela = new PdfPTable(3);
				//Cabeçalho
				PdfPCell col1 = new PdfPCell(new Paragraph("Nome"));
				PdfPCell col2 = new PdfPCell(new Paragraph("Fone"));
				PdfPCell col3 = new PdfPCell(new Paragraph("Email"));
				tabela.addCell(col1);
				tabela.addCell(col2);
				tabela.addCell(col3);
				//Popular a tabela com os contatos
				ArrayList<JavaBeans> lista = dao.listarContatos();
				for(int i=0;i< lista.size(); i++) {
					tabela.addCell(lista.get(i).getNome());
					tabela.addCell(lista.get(i).getFone());
					tabela.addCell(lista.get(i).getEmail());
				}
				documento.add(tabela);
				documento.close();
				
			} catch (Exception e) {
				System.out.println(e);
				documento.close();
			}
	}

}
