export default function InputField({ label, unit, value, onChange, name, type = 'text', error, step, options }) {
  return (
    <div className="field">
      <label htmlFor={name}>{label}</label>
      <div className="input-wrap">
        {options ? (
          <select id={name} name={name} value={value} onChange={onChange}>
            {options.map((opt) => (
              <option key={opt} value={opt}>{opt}</option>
            ))}
          </select>
        ) : (
          <>
            <input id={name} name={name} type={type} step={step} value={value} onChange={onChange} />
            {unit && <span className="unit">{unit}</span>}
          </>
        )}
      </div>
      {error && <div className="field-error">{error}</div>}
    </div>
  )
}
