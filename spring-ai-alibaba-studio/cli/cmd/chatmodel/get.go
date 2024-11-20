// Copyright 2024 the original author or authors.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package chatmodel

import (
	"fmt"
	"strings"

	"github.com/alibaba/spring-ai-alibaba/pkg/constant"
	"github.com/alibaba/spring-ai-alibaba/pkg/handler"
	"github.com/alibaba/spring-ai-alibaba/pkg/util/printer"
	"github.com/spf13/cobra"
)

// getCmd represents the list command
var getCmd = &cobra.Command{
	Use:   "get modelName...",
	Short: "Get chat model detail by model name",
	Long: `Get chat model detail by model name

Arguments:
[modelName]  Name of the chat models to get details for (required)
`,
	Example: fmt.Sprintf(`  # Get details of the chat model named "model1"
  %s %s get model1
  # Get details of the chat model named "model1" and "model2"
  %s %s get model1 model2
		`, constant.RootCmdName, chatModelCmdName, constant.RootCmdName, chatModelCmdName),
	Args: cobra.MinimumNArgs(1),
	Run: func(cmd *cobra.Command, args []string) {
		handler.NewChatModelHandlerManager().ChatModelGetHandler(cmd, args)
	},
}

func init() {
	// Here you will define your flags and configuration settings.

	// Cobra supports Persistent Flags which will work for this command
	// and all subcommands, e.g.:
	// listCmd.PersistentFlags().String("foo", "", "A help for foo")

	// Cobra supports local flags which will only run when this command
	// is called directly, e.g.:
	// listCmd.Flags().BoolP("toggle", "t", false, "Help message for toggle")
	getCmd.Flags().StringP(constant.OutputFlag, "o", string(printer.YamlPrinterKind), fmt.Sprintf("Output format supported values: %s", strings.Join(printer.PrinterDetailKindsAsString(), ", ")))
}
