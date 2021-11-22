/**
 * Confirmação de exclusão de um contato 
 * @author Luan Jesus
 * @param idcon
 */

function confirmar(idcon){
	let resposta = confirm("Confirma a exclulsão desse contato")
	if (resposta === true){
		window.location.href = "delete?idcon=" + idcon
	}
}