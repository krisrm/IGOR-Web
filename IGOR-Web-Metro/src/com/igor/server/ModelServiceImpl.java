package com.igor.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;

import template.Constraint;
import template.Option;
import template.Template;
import template.Template.VIterator;
import template.Variable;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.igor.client.ModelService;
import com.igor.shared.TemplateDTO;
import com.igor.shared.User;
import com.igor.shared.VariableDTO;
import com.igor.shared.VariableDTO.VarType;

import editor.XMLBuilder;
import generator.scripted.ScriptedGenerator;
import generator.scripted.TemplateParser;
import generator.scripted.TestItem;

/**
 * The server side implementation of the RPC service.
 */

@SuppressWarnings("serial")
public class ModelServiceImpl extends RemoteServiceServlet implements
		ModelService {
	private final static Logger LOG = Logger.getLogger(ModelServiceImpl.class
			.getName());
	private Map<String, User> sessions;

	public static String getPath(String path, ServletContext sc) {
		return sc.getRealPath(path);
	}

	public String getPath(String path) {
		return getPath(path, this.getServletContext());
	}

	public static TemplateDTO toDTO(Template t) {
		TemplateDTO r = new TemplateDTO();
		r.stem = t.stem();
		VIterator vI = t.vIterator();
		boolean constraints = true;
		while (vI.hasNext()) {
			Variable v = vI.next();
			VariableDTO vdt = new VariableDTO();
			vdt.setName(v.getName());
			if (constraints && v.getConstraints().length > 0) {
				for (Constraint c : v.getConstraints()) {
					VariableDTO constraint = new VariableDTO();
					constraint.constraint = c.toString();
					constraint.type = VarType.CONSTRAINT;
					r.variables.add(constraint);
				}
				constraints = false;
			}
			switch (v.type()) {
			case Variable.TEXT:
				vdt.type = VarType.TEXT;
				for (int i = 0; i < v.getNumKeys(); i++) {
					String text = v.getText2(i);
					if (VariableDTO.isImg(text)) {
						vdt.type = VarType.IMAGE;
						vdt.addImg(text);
					} else {
						vdt.keys.add(text);
					}
				}
				break;
			case Variable.NUMBER:
				vdt.type = VarType.NUMERIC;
				vdt.min = v.getRange()[0];
				vdt.max = v.getRange()[1];
				vdt.step = v.getStep();
				break;
			default:
				break;
			}
			r.variables.add(vdt);
		}
		return r;
	}

	public static Template toModel(TemplateDTO template) {
		List<Variable> variables = new ArrayList<Variable>();
		List<Option> keys = new ArrayList<Option>();
		List<Option> distractors = new ArrayList<Option>();
		List<Constraint> constraints = new ArrayList<Constraint>();
		int i = 1;
		for (String cons : template.getConstraints()) {
			constraints.add(new Constraint(i, cons));
			i++;
		}

		boolean setConstraints = false;
		for (VariableDTO vdt : template.variables) {

			Variable v = null;
			if (vdt.type == null) {
				continue;
			}
			switch (vdt.type) {
			case NUMERIC:
				v = new Variable(vdt.getName(), 0, vdt.getRange(), vdt.step,
						new Constraint[] {});
				break;
			case TEXT:
				v = new Variable(vdt.getName(), 0, vdt.getIntKeysText(),
						vdt.getTextKeys());
				break;
			case IMAGE:
				v = new Variable(vdt.getName(), 0, vdt.getIntKeysUrl(),
						vdt.getWrappedUrls());

				break;
			default:

				break;
			}
			if (v != null) {
				if (!setConstraints) {
					setConstraints = true;
					for (Constraint c : constraints) {
						v.addConstraint(c);
					}
				}

				variables.add(v);

			}
		}

		return new Template(12, template.stem, null,
				variables.toArray(new Variable[] {}),
				keys.toArray(new Option[] {}),
				distractors.toArray(new Option[] {}));
	}

	@Override
	public String generate(TemplateDTO template) {
		try {
			User u = currentUser();
			if (u == null || u.name.equals("guest")) {
				return null;
			}
			// new ScriptedGenerator().generate(useAnsKey, optionOrdering,
			// templateFile, itemBankFile, ansKeyFile, maxItems, numOfOptions,
			// SampleItem, sampleitemPath)

			List<Option> keys = new ArrayList<Option>();
			List<Option> distractors = new ArrayList<Option>();

			Template t = toModel(template);

			XMLBuilder xmlB = new XMLBuilder(t);
			// File itemBankFile = new File("xml/output" + Math.random() * 1000
			// + ".html");
			File itemBankFile = File.createTempFile("output", ".tmp");
			File templateFile = File.createTempFile("template", ".tmp");
			xmlB.saveToXML(templateFile);
			File ansKeyFile = File.createTempFile("ansKey", ".tmp");
			ScriptedGenerator sg = new ScriptedGenerator();
			sg.generate(true, TestItem.RANDOM, templateFile, itemBankFile,
					ansKeyFile, 0, distractors.size() + keys.size(), false, "");

			u.generate = FileUtils.readFileToByteArray(itemBankFile);
			saveUser(u);

			itemBankFile.deleteOnExit();
			templateFile.deleteOnExit();
			ansKeyFile.deleteOnExit();

			return "file?type=generate";
			// return itemBankFile.getPath();
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "generate error", e);
		}
		return null;
	}

	@Override
	public TemplateDTO upload() {
		try {
			User u = currentUser();
			if (u == null || u.upload == null) {
				return null;
			}

			// File xml = new File(getPath(url));
			File xml = File.createTempFile("xml", ".tmp");
			FileUtils.writeByteArrayToFile(xml, u.upload);
			Template t = new TemplateParser().loadTemplate(xml);
			xml.deleteOnExit();
			return toDTO(t);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "upload error", e);
		}

		return null;
	}

	@Override
	public String download(TemplateDTO template) throws Exception {
//		if (2+2==4)
//			throw new Exception("crazy");
		try {
			User u = currentUser();
			if (u == null || u.name.equals("guest")) {
				return null;
			}
			XMLBuilder b = new XMLBuilder(toModel(template));
			// String path = "xml/" + Math.random() * 10000 + ".xml";
			// File f = new File(getPath(path));
//			File f = File.createTempFile("download", ".tmp");
//			b.saveToXML(f);
//			u.download = FileUtils.readFileToByteArray(f);
			u.download = b.printToByteArray();
			saveUser(u);
//			f.deleteOnExit();
			return "file?type=download";
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "download error", e);
		}
		return null;
	}

	@Override
	public User login(String name, String password) {
		if (name.equals("mark") && password.equals("igoradmin")) {
			return newUser("mark");
		} else if (name.equals("guest") && password.equals("guestpassword")) {
			return newUser("guest");
		}

		return null;
	}

	protected User newUser(String name) {
		User r = new User();
		r.name = name;
		r.session = getSessionToken();
		if (sessions == null) {
			sessions = getSession();
		}
		if (sessions == null) {
			sessions = new HashMap<String, User>();
		}
		sessions.put(r.session, r);
		setSession(sessions);
		return r;
	}

	@SuppressWarnings("unchecked")
	private Map<String, User> getSession() {
		return (Map<String, User>) this.getServletContext().getAttribute(
				"session");
	}

	private void setSession(Map<String, User> s) {
		this.getServletContext().setAttribute("session", s);
	}

	private String getSessionToken() {
		return this.getThreadLocalRequest().getSession().getId();
	}

	@Override
	public void logout() {
		if (sessions == null) {
			sessions = getSession();
		}
		if (sessions == null)
			return;
		sessions.remove(getSessionToken());
	}

	@SuppressWarnings("unchecked")
	public static User currentUser(ServletContext sc, String sessionId) {
		Map<String, User> s = (Map<String, User>) sc.getAttribute("session");
		if (s == null)
			return null;
		return s.get(sessionId);
	}

	@SuppressWarnings("unchecked")
	public static void saveUser(ServletContext sc, User u, String sessionId) {
		Map<String, User> s = (Map<String, User>) sc.getAttribute("session");
		if (s == null) {
			return;
		}
		s.put(sessionId, u);
		sc.setAttribute("session", s);
	}

	private void saveUser(User u) {
		if (sessions == null) {
			sessions = getSession();
		}
		sessions.put(getSessionToken(), u);
		this.getServletContext().setAttribute("session", sessions);
	}

	private User currentUser() {
		if (sessions == null) {
			sessions = getSession();
		}
		if (sessions == null)
			return null;
		return sessions.get(getSessionToken());
	}

	@Override
	public User authenticate(String token) {
		if (sessions == null) {
			sessions = getSession();
		}
		if (sessions == null)
			return null;
		return sessions.get(token);
	}

	@Override
	public void logError(Throwable e) {
		LOG.log(Level.SEVERE, "Client side exception:", e);
	}

}
