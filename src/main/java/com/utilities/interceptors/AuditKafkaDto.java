package com.utilities.interceptors;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Entity class representing {@code Audit} in the system.
 * 
 * <p>
 * This class is mapped to {@code encryption_audit.audit} table in the database
 * to store the information provided by the APIs for auditing
 * </p>
 * 
 * <p>
 * This entity uses annotations such as:
 * </p>
 * 
 * <ul>
 * <li>{@code @Id} - To determine the primary key in the system</li>
 * <li>{@code @GeneratedValue(strategy = GenerationType.SEQUENCE)} - To generate
 * a unique identity for the primary key</li>
 * <li>{@code @Table()} - To map the table and schema to this {@code Entity}
 * class</li>
 * <li>{@code @Column()} - To map a particular column in the data base to an
 * object in this {@code Entity} class</li>
 * <li>{@code @NonNull} - To check null values for non-nnull fields in the data
 * base</li>
 * </ul>
 * 
 * <p>
 * Example usage:
 * </p>
 * 
 * <pre>
 * Audit audit = new Audit();
 * audit.setCode("Some value");
 * // Other operations
 * </pre>
 * 
 * @see <a href =
 *      "https://jakarta.ee/specifications/persistence/2.2/apidocs/javax/persistence/entity">
 *      Entity </a>
 * @author Kabir Akware
 */
@Getter
@ToString
@NoArgsConstructor
public class AuditKafkaDto {

	/**
	 * Private constructor to populate {@code Audit} class
	 * 
	 * @param date     Current time stamp
	 * @param endPoint API end point
	 * @param refNo    Reference number sent by user for a transaction
	 * @param status   HTTP status
	 * @param request  API request
	 * @param response API response
	 */
	private AuditKafkaDto(String date, String endPoint, Object refNo, Integer status, String request, String response) {
		this.date = date;
		this.api = endPoint;
		this.refNo = refNo;
		this.status = status;
		this.request = request;
		this.response = response;
	}

	/**
	 * API on which the request has been fired
	 */
	private String api;

	/**
	 * Reference number sent by user for a transaction
	 */
	private Object refNo;

	/**
	 * Request/response date
	 */
	private String date;

	/**
	 * HTTP status code returned in the response
	 */
	private Integer status;

	/**
	 * Data returned by system in case of a successful transaction
	 */
	private String request;

	/**
	 * Description returned by system in case of a unsuccessful transaction
	 */
	private String response;

	/**
	 * Method to get a new instance of {@code Audit} class
	 * 
	 * @param date     Current time stamp
	 * @param uri API end point
	 * @param object    Reference number sent by user for a transaction
	 * @param status   HTTP status
	 * @param request  API request
	 * @param response API response
	 * @return new instance of {@code Audit} class
	 */
	public static AuditKafkaDto getInstance(String date, String uri, Object object, Integer status, String request,
			String response) {
		return new AuditKafkaDto(date, uri, object, status, request, response);
	}

}
