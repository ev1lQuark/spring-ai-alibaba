package printer

import (
	"io"

	"gopkg.in/yaml.v3"
)

type YamlPrinter[T any] struct {
	writer io.Writer
}

func NewYamlPrinter[T any](output io.Writer) *YamlPrinter[T] {
	return &YamlPrinter[T]{
		writer: output,
	}
}

func (p *YamlPrinter[T]) PrintOne(data T) error {
	return p.print(data)
}

func (p *YamlPrinter[T]) PrintSlice(data []T) error {
	return p.print(data)
}

func (p *YamlPrinter[T]) print(data any) error {
	yamlData, err := yaml.Marshal(data)
	if err != nil {
		return err
	}
	_, err = p.writer.Write(yamlData)
	return err
}
